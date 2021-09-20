pipelines = ["first-pipeline", "another-pipeline"]
pipelines.each { pipeline ->
    println "Creating pipeline ${pipeline}"
    create_pipeline(pipeline)
}
def create_pipeline(String name) {
    pipelineJob(name) {
        definition {
            cps {
                sandbox(true)
                script("""
pipeline {
    agent any
    stages {
        stage("Hello") {
            steps {
                echo "Hello from pipeline ${name}"
            }
        }
        stage("Goodbye") {
            steps {
                echo "Goodbye from pipeline ${name}"
            }
        }
    }
}
""")
            }
        }
    }
}