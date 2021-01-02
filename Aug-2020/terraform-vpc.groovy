properties([
    parameters([
        choice(choices: ['Plan', 'Apply', 'Destroy'], description: '', name: 'ACTION'),
        choice(choices: ['Dev', 'QA', 'Prod'], description: '', name: 'ENVIRONMENT')
        ])
    ])

node("terraform"){

    def vpc_env='dev'
    def vpc_region='us-east-1'
    if(params.ENVIRONMENT == 'QA'){
        vpc_env='qa'
        vpc_region='us-east-2'
    }
    else if (params.ENVIRONMENT == 'Prod'){
        vpc_env='prod'
        vpc_region='us-west-2'
    }

    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins_aws_keys', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${vpc_region}"]) {
            stage("Terrraform Init"){
                sh """
                    source setenv.sh ${vpc_env}.tfvars
                    terraform init
                """
            }        
            
            if(params.ACTION == 'Destroy'){
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${vpc_env}.tfvars -auto-approve
                    """
                }
            }
            else if(params.ACTION == 'Apply'){
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file ${vpc_env}.tfvars -auto-approve
                    """
                }
            }
            else if(params.ACTION == 'Plan') {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file ${vpc_env}.tfvars
                    """
                }
            }       
        }
    }    
}
