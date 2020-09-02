properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: '', name: 'environment')
    ])
])

def aws_region_var = ''

if(params.environment == "dev"){
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa"){
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod"){
    aws_region_var = "us-west-2"
}

node {
    stage('Pull Repo') {
        git url: 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=apache-${UUID.randomUUID().toString()}"]) {
            stage('Packer Validate') {
                sh 'packer validate apache.json'
            }

            stage('Packer Build') {
                sh 'packer build apache.json | tee output.txt'

                def ami_id = sh(script: 'cat output.txt | grep us-east-2 | awk \'{print $2}\'', returnStdout: true)
                println(ami_id)
            }
        }  
    }
}

