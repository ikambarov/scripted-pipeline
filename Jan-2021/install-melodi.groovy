node{
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage("Ansible"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i "165.227.75.236," main.yml
            """
        }
    }
}