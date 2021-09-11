node("worker1"){
    stage("Intall Epel"){
        withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {
            sh '''
                ssh -o StrictHostKeyChecking=no -i $SSHKEY root@TEST-VM-IP "hostname"
            '''
        }        
    }
}