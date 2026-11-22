#include "Engine.hpp"

std::string SystemEngine::apply(std::unique_ptr<Operation> op) {
    if (!op) {
        return "Error: Null Operation";
    }
    state_ = op->execute(state_);
    std::string opName = op->name();
    history_.push_back(std::move(op));
    return opName + " -> State: " + std::to_string(state_) +
           " (Steps: " + std::to_string(history_.size()) + ")";
}

std::string SystemEngine::reset() {
    state_ = 0;
    history_.clear();
    return "State: 0 (Reset)";
}
