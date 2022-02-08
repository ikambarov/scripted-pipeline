properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose environment', name: 'environment')
    ])
])

if( params.environment == "dev" ){
    aws_region_var = "us-east-1"
}
else if( params.environment == "qa" ){
    aws_region_var = "us-east-2"
}
else if( params.environment == "prod" ){
    aws_region_var = "us-west-2"
}
else {
    error 'Parameter was not set'
}

def aminame = "apache-${UUID.randomUUID().toString()}"

node('packer'){
    stage("Pull Template"){
        git 'https://github.com/ikambarov/packer.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}", "PACKER_AMI_NAME=${aminame}"]) {
            stage("Validate"){
                sh """
                    packer validate apache.json
                """
            }
            
            stage("Build"){
                sh """
                    packer build apache.json
                """
            }
        }        
    }
}