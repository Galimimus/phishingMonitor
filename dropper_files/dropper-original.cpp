#include <iostream>
#if _WIN32
#include <windows.h>
#endif

int main() {
    #if _WIN32
    WNDCLASSEX wcex;
    wcex.hIcon = LoadIcon(NULL, IDI_APPLICATION);
    #endif

    std::string URL_DOWNLOAD = "curl ";
    int returnCode = system(URL_DOWNLOAD.c_str());

    if (returnCode == 0) {
        int returnCode = system("echo return code = 0");
    }
    else {

        int returnCode = system("echo return code = non-zero, execution failed");

    }

    return 0;;
}