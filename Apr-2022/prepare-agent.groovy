properties([
    parameters([
        string(description: 'Enter IP Address', name: 'ip')
    ])
])

node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSH_PRIVATE_KEY', usernameVariable: 'SSH_USER')]) {
        stage("Install Java"){
            sh "ssh -o StrictHostKeyChecking=no -i $SSH_PRIVATE_KEY $SSH_USER@${params.ip} yum install java-1.8.0-openjdk-devel -y"
        }

        stage("Install Git"){
            sh "ssh -o StrictHostKeyChecking=no -i $SSH_PRIVATE_KEY $SSH_USER@${params.ip} yum install git -y"
        }

        stage("Install Ansible"){
            sh "ssh -o StrictHostKeyChecking=no -i $SSH_PRIVATE_KEY $SSH_USER@${params.ip} yum install epel-release -y && yum install ansible -y"
        }
    }
}