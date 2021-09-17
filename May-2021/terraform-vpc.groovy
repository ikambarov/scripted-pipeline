properties([
    parameters([
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an ENV from the list', name: 'environment'),
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

node("worker1"){
    stage("Pull"){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withEnv(["AWS_REGION=${region}"]) {
        withCredentials([usernamePassword(credentialsId: 'aws-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            stage("Init"){
                sh """
                    #!/bin/bash
                    source ./setenv.sh ${params.environment}.tfvars 
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