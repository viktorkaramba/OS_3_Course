#ifndef LABORATORY_1_PROCESS_H
#define LABORATORY_1_PROCESS_H
#include <cstdlib>
#include <unistd.h>
#include <cstdio>
#include <signal.h>
#include <string>

class Process {
private:
    std::string name;
    pid_t process;
public:
    Process();
    Process(std::string name);
    pid_t GetProcess();
    std::string GetName();
    void SetName(std::string name);
    bool is_running();
    void start();
    void kill();
    void write(int x);
    ~Process() = default;
};



#endif //LABORATORY_1_PROCESS_H
