properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terrform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terrform destroy', name: 'terraform_destroy'),
        choice(choices: ['dev', 'qa', 'prod'], description: '', name: 'environment'),
        string(defaultValue: '', description: 'Provide AMI NAME', name: 'ami_name', trim: true)
    ])
])

def aws_region_var = ''

if(params.environment == "dev"){
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa"){
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod"){
    aws_region_var = "us-west-2"
}

// Replace bucket name, ssh key
def tf_vars = """
    s3_bucket = \"jenkins-terraform-evolvecybertraining\"
    s3_folder_project = \"terraform\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"terraform-ec2-by-ami-name\"
    s3_tfstate_file = \"infrastructure.tfstate\"

    environment = \"${params.environment}\"
    region      = \"${aws_region_var}\"
    public_key  = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local\"
    ami_name      = \"${params.ami_name}\"
"""

node("terraform"){
    stage("Pull Repo"){
        cleanWs()
        git url: 'https://github.com/ikambarov/terraform-ec2-by-ami-name.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                writeFile file: "${params.environment}.tfvars", text: "${tf_vars}"
                sh """
                    sh setenv.sh ${params.environment}.tfvars
                    terraform init
                """
            }        
            
            if (terraform_apply.toBoolean()) {
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }
            else if (terraform_destroy.toBoolean()) {
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${params.environment}.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file ${params.environment}.tfvars
                    """
                }
            }
        }        
    }    
}
