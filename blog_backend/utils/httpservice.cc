#include "httpservice.h"
#include <random>

namespace httpService
{

optional<string> saveImageFromReq(const HttpRequestPtr &req)
{
    MultiPartParser fileUpload;

    if (fileUpload.parse(req) != 0 || fileUpload.getFiles().size() != 1)
    {
        return nullopt;
    }

    auto &file = fileUpload.getFiles()[0];
    // auto md5 = file.getMd5();
    string filename = httpService::generateUUID();

    try {
        file.saveAs(filename);
    } catch (const exception &e) {
        return nullopt;
    }

    return optional<string>(filename);
}

string generateUUID()
{
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<uint32_t> dist(0, 0xFFFFFFFF);

    uint32_t data[4];
    for (auto& d : data) {
        d = dist(gen);
    }

    // Convert to UUID version 4 format
    data[1] = (data[1] & 0xFFFF0FFF) | 0x00004000; // Version 4 (0100)
    data[2] = (data[2] & 0x3FFFFFFF) | 0x80000000; // Variant 1 (10xx)

    std::stringstream uuid;
    uuid << std::hex << std::setfill('0') << std::nouppercase;

    uuid << std::setw(8) << data[0] << "-"
         << std::setw(4) << ((data[1] >> 16) & 0xFFFF) << "-"
         << std::setw(4) << (data[1] & 0xFFFF) << "-"
         << std::setw(4) << ((data[2] >> 16) & 0xFFFF) << "-"
         << std::setw(4) << (data[2] & 0xFFFF) << std::setw(8) << data[3];

    return uuid.str();
}

}
