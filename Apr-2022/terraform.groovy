properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an Environment', name: 'environment'),
        choice(choices: ['apply', 'destroy'], description: 'Choose a Terraform Action', name: 'action'),
        string(description: 'Enter an AMI NAME', name: 'ami_name')
    ])
])

if( params.environment == "dev" ) {
    region = "us-east-1"
}
else if( params.environment == "qa" ) {
    region = "us-east-2"
}
else {
    region = "us-west-2"
}

tfvars = """
environment   = \"${params.environment}\"
s3_bucket     = \"jenkins-terraform-evolvecybertraining\"
s3_folder_project = \"terraform_ec2\"
s3_folder_region = \"us-east-1\"
s3_folder_type = \"class\"
s3_tfstate_file = \"infrastructure.tfstate\"
region        = \"${region}\"	
public_key    = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local\"	
ami_name        = \"${params.ami_name}\"
"""

template = '''
apiVersion: v1
kind: Pod
metadata:
  name: terraform
spec:
  containers:
  - image: ikambarov/terraform:0.14
    name: terraform
    '''

podTemplate(cloud: 'kubernetes', label: 'terraform', showRawYaml: false, yaml: template) {
    node("terraform"){
        container("terraform"){
            stage("Pull Code"){
                git 'https://github.com/ikambarov/terraform-ec2-by-ami-name.git'
            }

            withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                stage("Init"){
                    writeFile file: 'my.tfvars', text: tfvars
                    sh "cat my.tfvars"
                    sh "/bin/sh setenv.sh my.tfvars"
                    sh "terraform init"
                }
                
                if(params.action == "apply") {
                    stage("Apply"){
                        sh "terraform apply -var-file my.tfvars -auto-approve"
                    }
                }

                else {
                    stage("Destroy"){
                        sh "terraform destroy -var-file my.tfvars -auto-approve"
                    }
                }
            }
        }
    }
}
