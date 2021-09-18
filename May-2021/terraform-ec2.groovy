properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an ENV from the list', name: 'environment'),
        string(description: 'Provide AMI name', name: 'ami_name', trim: true),
        booleanParam(defaultValue: true, description: 'Do you want to apply?', name: 'command')        
        ])
    ])

if(params.environment == 'dev'){
    region="us-east-1"
}
else if(params.environment == 'qa'){
    region="us-east-2"
}
else{
    region="us-west-2"
}

tfvars="""
    s3_bucket = \"jenkins-terraform-evolvecybertraining\"
    s3_folder_project = \"terraform\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"terraform-ec2-by-ami-name\"
    s3_tfstate_file = \"infrastructure.tfstate\"

    environment = \"${params.environment}\"
    region      = \"${region}\"
    public_key  = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local\"
    ami_name      = \"${params.ami_name}\"

"""

node("worker1"){
    stage("Pull"){
        cleanWs()
        git 'https://github.com/ikambarov/terraform-ec2-by-ami-name.git'
    }

    withEnv(["AWS_REGION=${region}"]) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Init"){
                writeFile file: "${params.environment}.tfvars", text: "$tfvars"

                sh """
                    #!/bin/bash
                    source ./setenv.sh ${params.environment}.tfvars 

                    cat ${params.environment}.tfvars

                    terraform init
                """
            }

            if(params.command == true){
                stage("Plan"){
                    sh """
                        terraform plan -var-file ${params.environment}.tfvars
                    """
                }

                stage("Apply"){
                    sh """
                        terraform apply -auto-approve -var-file ${params.environment}.tfvars
                    """
                }
            }
            else{
                stage("Destroy"){
                    sh """
                        terraform destroy -auto-approve -var-file ${params.environment}.tfvars
                    """
                }
            }            
        }
    }
}