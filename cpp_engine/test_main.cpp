#include "Engine.hpp"
#include <cassert>
#include <iostream>

int main() {
    SystemEngine engine;

    std::string r1 = engine.apply(std::make_unique<AddOperation>(10));
    std::cout << r1 << "\n";
    assert(engine.getState() == 10);

    std::string r2 = engine.apply(std::make_unique<MultiplyOperation>(4));
    std::cout << r2 << "\n";
    assert(engine.getState() == 40);
    assert(engine.getStepCount() == 2);

    std::cout << engine.reset() << "\n";
    assert(engine.getState() == 0);
    assert(engine.getStepCount() == 0);

    std::cout << "All OOP C++ checks passed cleanly!\n";
    return 0;
}
