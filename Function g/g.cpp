#include <iostream>
#include "../lab1_cpp/trialfuncs.hpp"
#include <sys/wait.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cstring>


volatile sig_atomic_t shutdown_flag = 1;

void GracefulExit(int signal_number)
{
    shutdown_flag = 0;
}

int main()
{
    // Register SIGTERM handler
    signal(SIGTERM, GracefulExit);
    int fd = open("fifo_g", O_RDONLY);
    int x;

    if (read(fd, &x, sizeof(int)) == -1) {
        std::cout<<"Error to read a fifo\n";
        return 2;
    }
    close(fd);
    std::cout<<os::lab1::compfuncs::trial_g<os::lab1::compfuncs::INT_SUM>(x);
    int fifo;
    fifo = open("g_fifo_back", O_WRONLY);
    if(std::holds_alternative<os::lab1::compfuncs::hard_fail>(os::lab1::compfuncs::trial_g<os::lab1::compfuncs::INT_SUM>(x))){
//        std::string hard_fail = "function f finished with hard fail";
//        if(write(fifo,hard_fail.c_str(),strlen(hard_fail.c_str())) == -1){
//            std::cout<<"Error to write\n";
//            return 2;
//        }
//        return 2;
        raise(SIGTERM);
    }
    else{
        auto y = os::lab1::compfuncs::trial_g<os::lab1::compfuncs::INT_SUM>(x);
        if(write(fifo,&y,sizeof(y)) == -1){
            std::cout<<"Error to write\n";
            return 2;
        }
    }
    close(fifo);
    exit(EXIT_SUCCESS);
}