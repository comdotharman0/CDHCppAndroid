#pragma once
#include <memory>
#include <string>
#include <vector>

// Abstract Base Class demonstrating Polymorphism & Virtual Destructors
class Operation {
public:
    virtual ~Operation() = default;
    virtual long long execute(long long current) const = 0;
    virtual std::string name() const = 0;
};

class AddOperation : public Operation {
private:
    long long delta_;

public:
    explicit AddOperation(long long delta) : delta_(delta) {}
    long long execute(long long current) const override { return current + delta_; }
    std::string name() const override { return "ADD(" + std::to_string(delta_) + ")"; }
};

class MultiplyOperation : public Operation {
private:
    long long factor_;

public:
    explicit MultiplyOperation(long long factor) : factor_(factor) {}
    long long execute(long long current) const override { return current * factor_; }
    std::string name() const override { return "MUL(" + std::to_string(factor_) + ")"; }
};

// Encapsulated Engine managing polymorphic operations via RAII (std::unique_ptr)
class SystemEngine {
private:
    long long state_{0};
    std::vector<std::unique_ptr<Operation>> history_;

public:
    SystemEngine() = default;

    // Delete copy operations (unique_ptr is move-only)
    SystemEngine(const SystemEngine&) = delete;
    SystemEngine& operator=(const SystemEngine&) = delete;

    std::string apply(std::unique_ptr<Operation> op);
    std::string reset();
    long long getState() const { return state_; }
    size_t getStepCount() const { return history_.size(); }
};
