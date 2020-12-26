properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terrform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terrform destroy', name: 'terraform_destroy'),
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose Environment', name: 'environment'),
        string(defaultValue: '', description: 'Provide AMI Name', name: 'ami_name', trim: true),
        string(defaultValue: '', description: 'Provide Deployment Name', name: 'deployment_name', trim: true)
    ])
])

def aws_region_var = ''

if(params.environment == "dev") {
    println("Applying Dev")
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa") {
    println("Applying QA")
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod") {
    println("Applying Prod")
    aws_region_var = "us-west-2"
}

def tfvar = """
    s3_bucket = \"jenkins-terraform-evolvecybertraining\"
    s3_folder_project = \"${params.deployment_name}\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"class\"
    s3_tfstate_file = \"infrastructure.tfstate\"
    environment = \"${params.environment}\"

    region   = \"${aws_region_var}\"
    az1      = \"${aws_region_var}a\"
    az2      = \"${aws_region_var}b\"
    az3      = \"${aws_region_var}c\"

    vpc_cidr_block  = \"172.32.0.0/16\"

    public_cidr1    = \"172.32.1.0/24\"
    public_cidr2    = \"172.32.2.0/24\"
    public_cidr3    = \"172.32.3.0/24\"

    private_cidr1   = \"172.32.10.0/24\"
    private_cidr2   = \"172.32.11.0/24\"
    private_cidr3   = \"172.32.12.0/24\"

    public_key    = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3"	
    ami_name        = "${params.ami_name}"
"""

node("terraform"){
    stage("Pull Repo"){
        cleanWs()
        git url: 'https://github.com/ikambarov/terraform-ec2-by-ami-name.git'

        writeFile file: "${params.environment}.tfvars", text: "${tfvar}"
    }

    withCredentials([usernamePassword(credentialsId: 'aws_jenkins_key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                sh """
                    sh setenv.sh ${environment}.tfvars
                    terraform init
                """
            }        
            
            if (params.terraform_apply) {
                stage("Terraform Apply"){
                    sh """
                        terraform apply -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else if (params.terraform_destroy) {
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file ${environment}.tfvars
                    """
                }
            }
        }
    }    
}

