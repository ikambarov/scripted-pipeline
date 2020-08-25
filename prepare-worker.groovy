node {
    stage('Init') {
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY root@104.131.115.78 yum install epel-release -y'
        }
    }
}


