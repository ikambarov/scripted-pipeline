
def aws_region_var = ''
def environment = ''


if(env.BRANCH_NAME ==~ "dev.*"){
    aws_region_var = "us-east-1"
    environment = "dev"
}
else if(env.BRANCH_NAME ==~ "qa.*"){
    aws_region_var = "us-east-2"
    environment = "qa"
}
else if(env.BRANCH_NAME ==~ "master"){
    aws_region_var = "us-west-2"
    environment = "prod"
}

node {
    stage('Pull Repo') {
        git url: 'https://github.com/ikambarov/packer.git'
    }

    def ami_name = "apache-${UUID.randomUUID().toString()}"
    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${ami_name}"]) {
            stage('Packer Validate') {
                sh 'packer validate apache.json'
            }

            def ami_id = ''
            stage('Packer Build') {
                sh 'packer build apache.json | tee output.txt'

                ami_id = sh(script: "cat output.txt | grep ${aws_region_var} | awk \'{print \$2}\'", returnStdout: true).trim()
                println(ami_id)
            }

            stage('Create EC2 Instance'){
                build job: 'terraform-ec2', parameters: [
                    booleanParam(name: 'terraform_apply', value: true),
                    booleanParam(name: 'terraform_destroy', value: false),
                    string(name: 'environment', value: "${environment}"),
                    string(name: 'ami_id', value: "${ami_id}")
                    ]
            }
        }  
    }
}

