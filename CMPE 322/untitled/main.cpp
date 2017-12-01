#include <iostream>
#include <fstream>
#include <string>
#include <queue>

using namespace std;

//I created ofstream to write
ofstream outp("output.txt");


// defined program code structure for code.txt
class program_code_structure
{
public:
    string instr_name;
    int burst_time;

    program_code_structure() {} //Constructor
    program_code_structure(string instr_name__, int burst_time__)
    {
        instr_name = instr_name__;
        burst_time = burst_time__;

    }
};


//Defined process which include their attributes such as name,arrival time, instructions, which program code etc
class process_structure
{
public:
    string process_name;
    string which_program_code;
    int arrival_time;
    int totalBurstTime;
    queue<program_code_structure> InstructionQueue; // queue for Instruction

    process_structure() {}

    process_structure(string process_name__, string which_program_code__, int arrival_time__, int burst_time__,
                      queue<program_code_structure> ThreadQueue__)
    {
        process_name = process_name__;
        which_program_code = which_program_code__;
        arrival_time = arrival_time__;
        totalBurstTime = burst_time__;
        InstructionQueue = ThreadQueue__;
    }


};


static queue<program_code_structure> get_code(string whichCodeTextNumber)
{
    //this function get code from txt file for example 1.code.txt

    ifstream file(whichCodeTextNumber);
    string instr_name;
    int burst_time;
    queue<program_code_structure> tempQueue;
    //parsed file and splitted it according to "TAB" sign

    while (file >> instr_name >> burst_time)
    {
        program_code_structure tempCodeStructure(instr_name, burst_time);
        tempQueue.push(tempCodeStructure);
    }

    return tempQueue;
}

queue<process_structure> SetProcessInfo(queue<process_structure> processQueue)
{
//get info for process from definition text file
    ifstream myfile("definition.txt");

    string process_name;
    string which_program_code;
    int arrival_time;


    //parsed file and splitted it according to "TAB" sign

    while (myfile >> process_name >> which_program_code >> arrival_time)
    {

        process_structure pr;
        pr.process_name = process_name;
        pr.which_program_code = which_program_code;
        pr.arrival_time = arrival_time;

        pr.InstructionQueue = get_code(pr.which_program_code);

        processQueue.push(pr);

        //Add all attributes to processQueue
    }
    myfile.close();

    return processQueue;
}


void Schedule(queue<process_structure> processQueue)
{

    queue<process_structure> CPU;
    //I take first element of processQueue
    //than I added to CPU and removed from processQueue

    process_structure temp = processQueue.front();
    CPU.push(temp);
    int time = CPU.front().arrival_time;

    //Added first element to output.txt
    outp << time << "- " << "HEAD -" << processQueue.front().process_name << "- TAIL" << endl;
    //pop'ed first element
    processQueue.pop();


    //I checked if new process is include or CPU is not empty
    while (!processQueue.empty() || !CPU.empty())
    {

        int execTime = 0;
        //This while loop  control timeslot  is equal or more than it
        //because we cannot divide intructor of any process

        while (execTime < 100 && !CPU.front().InstructionQueue.empty())
        {
            execTime = execTime + CPU.front().InstructionQueue.front().burst_time;
            CPU.front().InstructionQueue.pop();
        }
        time += execTime;


        while (processQueue.front().arrival_time <= time && !processQueue.empty())
        {
            process_structure temp = processQueue.front();
            CPU.push(temp);
            processQueue.pop();
            //check if any process comes or not according to arrival time info

        }

        if (CPU.front().InstructionQueue.empty())
        {
            CPU.pop();
        }
        else
        {
            process_structure temp = CPU.front();
            CPU.pop();
            CPU.push(temp);

        }


        //For output txt

        outp << time << "::" << "HEAD ";
        queue<process_structure> tempQueue = CPU;
        // I created  a temporary file to print CPU;
        while (!tempQueue.empty())
        {
            outp << "-" << tempQueue.front().process_name;
            tempQueue.pop();
        }
        outp << "-TAIL " << endl;


    }


}

int main()
{

    //created Process queue
    queue<process_structure> processQueue;

    //the func take all information from files and assing process
    processQueue = SetProcessInfo(processQueue);

    //This Schedule decide which process must execute according to Timeslot and readyqueue FIFO
    //Round Robin
    Schedule(processQueue);


    return 0;
}

