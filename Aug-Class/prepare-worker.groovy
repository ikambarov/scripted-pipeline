node{
    properties([
        parameters([
            string(defaultValue: '', description: 'Provide Remote Node IP', name: 'node_ip', trim: true)
        ])
    ])

    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-ssh-key', keyFileVariable: 'SSH_KEY', passphraseVariable: '', usernameVariable: 'SSH_USERNAME')]) {
        stage('Install Prerequisites'){
            sh "ssh -o StrictHostKeyChecking=false -i $SSH_KEY root@${node_ip} yum install epel-release -y"
        }
        stage('Install Git'){
            sh "ssh -o StrictHostKeyChecking=false -i $SSH_KEY root@${node_ip} yum install git -y"
        }
        stage('Install Java'){
            sh "ssh -o StrictHostKeyChecking=false -i $SSH_KEY root@${node_ip} yum install java-1.8.0-openjdk-devel -y"
        }
        stage('Install Ansible'){
            sh "ssh -o StrictHostKeyChecking=false -i $SSH_KEY root@${node_ip} yum install ansible -y"
        }
    } 
}

