#include "Manager.h"

Manager::Manager(){
    this->f.SetName("../Function f/f");
    this->g.SetName("../Function g/g");
    input_value = input();
}

void Manager::run() {
    //this->f.start();
    this->g.start();
    pid_t f1;
    pid_t g1;
    if(!(f1 = fork())){
        char *arg_list[] = {f.GetName().data(), nullptr};
        execve(f.GetName().c_str(), arg_list, nullptr);
        perror("execve");
    }
    if(!(g1 = fork())){
        char *arg_list_[] = {g.GetName().data(),nullptr};
        execve(g.GetName().c_str(),arg_list_, nullptr);
        perror("execve");
        exit(EXIT_FAILURE);
    }
   else{
        unlink("fifo_f");
        Create_Fifo("fifo_f");
        int fifo = open("fifo_f", O_WRONLY);
        Write_Fifo(fifo);
        close(fifo);
        unlink("fifo_g");
        Create_Fifo("fifo_g");
        int fifo_ = open("fifo_g", O_WRONLY);
        Write_Fifo(fifo_);
        close(fifo_);
        waitpid(f1, NULL, 0);
        waitpid(g1, NULL, 0);
    }
}

void Manager::Create_Fifo(std::string name) {
    unlink(name.c_str());
    if(mkfifo(name.c_str(), 0777) == -1){
        std::cout<<"Error to create a fifo\n";
    }
}

void Manager::Write_Fifo(int fifo) {
    if(write(fifo,&input_value,sizeof(int)) == -1){
        std::cout<<"Error to write\n";
    }
}


int Manager::input() {
    std::cout<<"Enter input value"<<std::endl;
    int x;
    std::cin>>x;
    return x;
}



template<typename T>
bool Manager::is_hard_fail(T) {
    return false;
}
