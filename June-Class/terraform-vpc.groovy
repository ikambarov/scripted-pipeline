properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terraform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terraform destroy', name: 'terraform_destroy')
        whats the env?
    ])
])

node{
    stage("Pull Repo"){
        git branch: 'master', url: 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withEnv(['AWS_REGION=us-east-1']) {
        withCredentials([usernamePassword(credentialsId: 'aws_jenkins_key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Terraform Init"){
                sh """
                    bash setenv.sh dev.tfvars
                    terraform init 
                """
            }

            if(params.terraform_apply){
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file dev.tfvars -auto-approve
                    """
                }
            }
            else if(params.terraform_destroy){
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file dev.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file dev.tfvars
                    """
                }
            }           
        }
    }         
}
