properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], name: 'environment'),
        choice(choices: ['apply', 'destroy'], name: 'input')
    ])
])

if(params.environment == "dev"){
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa"){
    aws_region_var = "us-east-2"
}
else{
    aws_region_var = "us-west-2"
}

node("terraform"){
    
    stage("Pull code") {
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }
    
    withEnv(["AWS_REGION=${aws_region_var}"]) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Terraform init"){
                sh """
                    sh setenv.sh ${params.environment}.tfvars
                    terraform init
                """
            }

            stage("Terraform plan"){
                sh """
                    terraform plan -var-file ${params.environment}.tfvars
                """
            }

            if(params.input == "apply"){
                stage("Terraform apply"){
                    sh """
                        terraform apply -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }

            else{
                stage("Terraform destroy"){
                    sh """
                        terraform destroy -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }
        }
    }
}
