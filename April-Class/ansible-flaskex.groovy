properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true)
        ])
    ])

if (nodeIP.length() > 6) {
    node {
        stage('Pull Repo') {
            git branch: 'master', changelog: false, poll: false, url: 'https://github.com/ikambarov/ansible-Flaskex.git'
        }

        withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'FLASKEX_REPO=https://github.com/ikambarov/Flaskex.git', 'FLASKEX_BRANCH=master']) {
            stage("Install Prerequisites"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'prerequisites.yml'
                }
            stage("Pull Flaskex"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'pull_repo.yml'
                }
            stage("Install Python"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'install_python.yml'
                }
            stage("Start Flaskex"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'start_app.yml'
                }
        }  
    }
}
else {
    error 'Please enter valid IP address'
}


