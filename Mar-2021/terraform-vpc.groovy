properties([parameters([choice(choices: ['apply', 'destroy'], name: 'input')])])

node("terraform"){
    
    stage("Pull code") {
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }
    
    withEnv(['AWS_REGION=us-east-1']) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Terraform init"){
                sh '''
                    terraform init
                '''
            }

            AWS_REGION

            stage("Terraform plan"){
                sh '''
                    terraform plan -var-file dev.tfvars
                '''
            }

            if(params.input == "apply"){
                stage("Terraform apply"){
                    sh '''
                        terraform apply -var-file dev.tfvars -auto-approve
                    '''
                }
            }

            else{
                stage("Terraform destroy"){
                    sh '''
                        terraform destroy -var-file dev.tfvars -auto-approve
                    '''
                }
            }
        }
    }
    
    

    
}
