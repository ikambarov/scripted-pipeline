properties([
    parameters([
        string(description: 'Enter an IP address', name: 'ip')
    ])
])

node {
    stage("Pull Repo") {
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

    stage("Execute Playbook") {
        withCredentials([sshUserPrivateKey(credentialsId: 'ansible', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i \"${params.ip},\" --private-key $SSH_KEY main.yml
            """
        }
    }
}