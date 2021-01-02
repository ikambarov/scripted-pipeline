properties([
    parameters([
        choice(choices: ['Plan', 'Apply', 'Destroy'], description: '', name: 'ACTION'),
        choice(choices: ['Dev', 'QA', 'Prod'], description: '', name: 'ENVIRONMENT'),
        string(defaultValue: '', description: '', name: 'AMI_ID', trim: true)
        ])
    ])

node("terraform"){

    def ec2_env='dev'
    def ec2_region='us-east-1'

    if(params.ENVIRONMENT == 'QA'){
        ec2_env='qa'
        ec2_region='us-east-2'
    }
    else if (params.ENVIRONMENT == 'Prod'){
        ec2_env='prod'
        ec2_region='us-west-2'
    }

    def tfvar = """
        s3_bucket = "jenkins-terraform-evolvecybertraining"
        s3_folder_project = "terraform_ec2"
        s3_folder_region = "us-east-1"
        s3_folder_type = "class"
        s3_tfstate_file = "infrastructure.tfstate"
        
        environment = "${ec2_env}"
        region      = "${ec2_region}"
        public_key  = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local"
        ami_id      = "${params.AMI_ID}"
    """

    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/terraform-ec2.git'
    }

    withCredentials([usernamePassword(credentialsId: 'jenkins_aws_keys', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${ec2_region}"]) {
            stage("Terrraform Init"){
                writeFile file: "${ec2_env}.tfvars", text: "${tfvar}"
                sh """
                    source setenv.sh ${ec2_env}.tfvars
                    terraform init
                """
            }        
            
            if(params.ACTION == 'Destroy'){
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${ec2_env}.tfvars -auto-approve
                    """
                }
            }
            else if(params.ACTION == 'Apply'){
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file ${ec2_env}.tfvars -auto-approve
                    """
                }
            }
            else if(params.ACTION == 'Plan') {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file ${ec2_env}.tfvars
                    """
                }
            }       
        }
    }    
}
