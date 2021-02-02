properties([
    parameters([
            string(defaultValue: '', description: 'Enter IP Address', name: 'IP', trim: true)
        ])
    ])

node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
        stage('Install Preprequisites'){
            sh " ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USERNAME@${ params.IP } 'yum install epel-release -y' "
        }
        stage('Install Java'){
            sh " ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USERNAME@${ params.IP } 'yum install java-1.8.0-openjdk-devel -y' "
        }
        stage('Install git'){
            sh " ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USERNAME@${ params.IP } 'yum install git -y' "
        }
        stage('Install Ansible'){
            sh " ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USERNAME@${ params.IP } 'yum install ansible -y' "
        }
    }
}
