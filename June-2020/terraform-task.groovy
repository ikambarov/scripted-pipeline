properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terraform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terraform destroy', name: 'terraform_destroy')
    ])
])

node{
    stage("Pull Repo"){
        git branch: 'solution', url: 'https://github.com/ikambarov/terraform-task.git'
    }

    dir('sandbox/') {
        withCredentials([usernamePassword(credentialsId: 'aws_jenkins_key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Terraform Init"){
                sh """
                    terraform init 
                """
            }

            if(params.terraform_apply){
                stage("Terraform Apply"){
                    sh """
                        terraform apply -auto-approve
                    """
                }
            }
            else if(params.terraform_destroy){
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform plan
                    """
                }
            }           
        }        
    }
}
