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

            stage("Terraform Plan"){
                sh "terraform plan -var-file ${params.environment}.tfvars"
            }
            
            stage("Terraform Apply"){
                sh "terraform apply -var-file ${params.environment}.tfvars -auto-approve"
            }
        }        
    }
}
