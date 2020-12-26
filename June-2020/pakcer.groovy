properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose Environment', name: 'environment')
    ])
])

def aws_region_var = ''

if(params.environment == "dev") {
    println("Applying Dev")
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa") {
    println("Applying QA")
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod") {
    println("Applying Prod")
    aws_region_var = "us-west-2"
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
                build wait: false, job: 'terraform-ec2', parameters: [
                    booleanParam(name: 'terraform_apply', value: true),
                    booleanParam(name: 'terraform_destroy', value: false),
                    string(name: 'environment', value: "${params.environment}"),
                    string(name: 'ami_name', value: "${ami_name}")
                    ]
            }
        }     
    }
}
