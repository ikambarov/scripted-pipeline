properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose Environment', name: 'environment')
    ])
])

def aws_region_var = ''

if(params.environment == "dev") {
    println("Creating image at Dev")
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa") {
    println("Creating image at QA")
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod") {
    println("Creating image at Prod")
    aws_region_var = "us-west-2"
}

def random_name = "${params.environment}-${ UUID.randomUUID().toString()}"

node("packer"){
    stage('Pull Repo') {
        git url: 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins_aws_keys', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${random_name}"]) {
            stage('Packer Validate') {
                sh 'packer validate apache.json'
            }

            stage('Packer Build') {
                sh 'packer build apache.json'
            }
        }
    }
}