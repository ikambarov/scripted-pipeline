node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage('Init') {
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@104.131.115.78 yum install epel-release -y'
        }
        stage("Install git") {
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@104.131.115.78 yum install git -y'
        }
        stage("Install Java"){
            sh 'ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@104.131.115.78 yum install java-1.8.0-openjdk-devel -y'
        }
    }
}
