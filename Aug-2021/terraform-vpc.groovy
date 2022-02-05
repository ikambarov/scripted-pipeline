properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose environment', name: 'environment'),
        choice(choices: ['plan', 'apply', 'destroy'], description: 'Choose Terraform Action', name: 'terraformaction')
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

node('terraform'){
    stage("Pull Code"){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terraform Init"){
                sh """
                    #!/bin/bash
                    source ./setenv.sh ${params.environment}.tfvars

                    terraform init
                """
            }   
        
            if( params.terraformaction == "plan" ){
                stage("Terraform Plan"){
                    sh "terraform plan -var-file ${params.environment}.tfvars"
                }
            }
            
            if( params.terraformaction == "apply" ){
                stage("Terraform Apply"){
                    sh "terraform apply -var-file ${params.environment}.tfvars -auto-approve"
                }
            }

            if( params.terraformaction == "destroy" ){
                stage("Terraform Destroy"){
                    sh "terraform destroy -var-file ${params.environment}.tfvars -auto-approve"
                }
            }
        }        
    }
}
