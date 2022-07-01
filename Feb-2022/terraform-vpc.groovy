properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Please choose an ENV', name: 'environment'),
        choice(choices: ['apply', 'destroy'], name: 'action')
    ])
])

if(params.environment == 'dev'){
    region = "us-east-1"
}
else if(params.environment == 'qa'){
    region = "us-east-2"
}
else{
    region = "us-west-2"
}

node("terraform"){
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withEnv(["AWS_REGION=${ region }"]) {
        withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Init"){
                sh """
                    bash setenv.sh ${ params.environment }.tfvars
                    terraform init
                """
            }
            
            stage("Plan"){
                sh """
                    terraform plan -var-file ${ params.environment }.tfvars
                """
            }

            if(params.action == 'apply'){
                stage("Apply"){
                    sh """
                        terraform apply -var-file ${ params.environment }.tfvars -auto-approve
                    """
                }
            }
            else{
                stage("Destroy"){
                    sh """
                        terraform destroy -var-file ${ params.environment }.tfvars -auto-approve
                    """
                }
            }            
        }
    } 
}