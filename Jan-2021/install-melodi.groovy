properties([
    parameters([
        string(defaultValue: '', description: 'Enter IP Address', name: 'IPADDRESS', trim: true)
        ])
    ])

node{
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'SSHKEY', passphraseVariable: '', usernameVariable: 'SSHUSERNAME')]) {
        stage("Ansible"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook --private-key $SSHKEY -i "${params.IPADDRESS}," main.yml
            """
        }
    }
}