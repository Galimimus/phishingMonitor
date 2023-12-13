#include <iostream>
#if _WIN32
#include <windows.h>
#endif

int main() {
/*    #if _WIN32
    WNDCLASSEX wcex;
    wcex.hIcon = LoadIcon(NULL, IDI_APPLICATION);
    #endif*/
    //setlocale(LC_ALL, "en_US.UTF-8");

    const char* x = "/C curl ";
    ShellExecute(0, "open", "cmd.exe", x, 0, SW_HIDE);

    return 0;
}