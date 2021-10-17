
#ifndef LABORATORY_1_MANAGER_H
#define LABORATORY_1_MANAGER_H

#include <iostream>
#include <string>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "Process.h"

class Manager {
public:
    Manager();
    ~Manager() = default;
    void run();
    int input();
    void Create_Fifo(std::string name);
    void Write_Fifo(int fifo);
    void Read_Fifo(std::string name);
    template<typename T>
    bool is_hard_fail(T);
private:
    Process f;
    Process g;
    int input_value;
};


#endif //LABORATORY_1_MANAGER_H
