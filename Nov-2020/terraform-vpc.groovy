properties([
    parameters([
        booleanParam(defaultValue: false, description: 'Do you want to run Terraform apply?', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run Terraform destroy?', name: 'terraform_destroy'),
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose environment: ', name: 'environment')
        ])
    ])
node{
    def aws_region_var = ''

    if(params.environment == 'dev'){
        println("Applying for dev")
        aws_region_var = 'us-east-1'
    }
    else if(params.environment == 'qa'){
        println("Applying for qa")
        aws_region_var = 'us-east-2'
    }
    else{
        println("Applying for prod")
        aws_region_var = 'us-west-2'
    }

    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                sh """
                    bash setenv.sh ${params.environment}.tfvars
                    terraform init
                    terraform plan -var-file dev.tfvars
                """
            }        
            
            if(params.terraform_apply){
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }
            else if(params.terraform_destroy){
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }
        }        
    }    
}
