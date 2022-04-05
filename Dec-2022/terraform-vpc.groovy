properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an Environment', name: 'environment')
        ])
    ])

if( params.environment == 'dev' ) {
    vpc_region = "us-east-1"
}

else if( params.environment == 'qa' ) {
    vpc_region = "us-east-2"
}

else {
    vpc_region = "us-west-2"
}

node {
    stage('Pull Code'){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${vpc_region}"]) {
            stage('Terraform Init'){
                sh """
                    source ./setenv.sh ${params.environment}.tfvars
                    terraform init
                """
            }

            stage('Terraform Plan'){
                sh """
                    terraform plan -var-file ${params.environment}.tfvars
                """
            }

            stage('Terraform Apply'){
                sh """
                    terraform apply -var-file ${params.environment}.tfvars -auto-approve
                """
            }
        }
    }
}