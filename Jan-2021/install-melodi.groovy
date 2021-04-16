properties([
    parameters([
        string(defaultValue: '', description: 'Enter IP Address', name: 'IPADDRESS', trim: true)
        ])
    ])

node{
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }
    
    stage("Ansible"){
        ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', disableHostKeyChecking: true, inventory: "${params.IPADDRESS},", playbook: 'main.yml'
    }
}