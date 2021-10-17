
#include "Process.h"
Process::Process() {
    this->name = "unknown";
}
Process::Process(std::string name) {
    this->name = name;
}

void Process::SetName(std::string name){
    this->name = name;
}

pid_t Process::GetProcess(){
    return process;
}

std::string Process::GetName(){
    return name;
}

void Process::start() {
    process = fork();
    if (process == -1) {
        perror("fork");
        exit(EXIT_FAILURE);
    }

}

void Process::kill() {
    ::kill(process, SIGTERM);
}

bool Process::is_running() {
    if(process > 0){
        return false;
    }
    else{
        return true;
    }
}

