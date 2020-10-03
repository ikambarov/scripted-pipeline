def aws_region_var = ''
def environment = ''

if(BRANCH_NAME ==~ "dev.*") {
    println("Applying Dev")
    aws_region_var = "us-east-1"
    environment = "dev"
}
else if(BRANCH_NAME ==~ "qa.*") {
    println("Applying QA")
    aws_region_var = "us-east-2"
    environment = "qa"
}
else if(BRANCH_NAME == "master") {
    println("Applying Prod")
    aws_region_var = "us-west-2"
    environment = "prod"
}
else {
    error("Branch name didn't match RegEx")
}

node {
    stage('Pull Repo') {
        git 'https://github.com/ikambarov/packer.git'
    }

    def ami_name = "apache-${UUID.randomUUID().toString()}"

    withCredentials([usernamePassword(credentialsId: 'aws_jenkins_key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${ami_name}"]) {
            stage('Packer Validate') {
                sh 'packer validate apache.json'
            }

            stage('Packer Build') {
                sh 'packer build apache.json'
            }   

            stage('Build EC2 Instance') {
                build wait: false, job: 'terraform-ec2', parameters: [booleanParam(name: 'terraform_apply', value: true), booleanParam(name: 'terraform_destroy', value: false), string(name: 'environment', value: "${environment}"), string(name: 'ami_name', value: "${ami_name}")]
            }
        }     
    }
}
