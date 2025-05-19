package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.instruction.*;
import edu.kit.kastel.vads.compiler.backend.register.*;
import edu.kit.kastel.vads.compiler.ir.node.*;
import org.jspecify.annotations.Nullable;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class AssemblyGenerator {
    private final StringBuilder builder = new StringBuilder();
    private final RegisterMapping registerMapping;
    private @Nullable VirtualRegister storedInTemp;
    private final int maxStackUsage;

    public AssemblyGenerator(InstructionBlock block, RegisterMapping registerMapping, int maxStackUsage) {
        this.registerMapping = registerMapping;
        this.maxStackUsage = maxStackUsage;
        storedInTemp = null;
        addStarterCode();
        if (maxStackUsage > 0)
            builder.append(String.format("subq $%d, %%rsp\n", maxStackUsage));
        for (Instruction instruction : block.getInstructions()) {
            generateForInstruction(instruction);
        }
    }

    public String getAssembly() {
        return builder.toString();
    }

    private void addStarterCode() {
        builder.append(".global main\n" +
                ".global _main\n" +
                ".text\n" +
                "main:\n" +
                "call _main\n" +
                "movq %rax, %rdi\n" +
                "movq $0x3C, %rax\n" +
                "syscall\n" +
                "_main:\n");
    }

    private void generateForInstruction(Instruction instruction) {
        switch (instruction) {
            case AddInstruction add -> binary(add, "addl", true);
            case SubInstruction sub -> binary(sub, "subl", false);
            case MulInstruction mul -> binary(mul, "imull", true);
            case CtldInstruction _ -> ctld();
            case DivModInstruction dm -> divMod(dm);
            case ReturnInstruction r -> returnInstruction(r);
            case ConstIntInstruction c -> constInt(c);
            case MoveInstruction m -> move(m.getSource(registerMapping), m.getDestination(registerMapping));
        }
    }

    private void binary(BinaryOperationInstruction node, String assemblyInstructionName, boolean commutative) {
        Register destination = node.getDestination(registerMapping);
        Register left = node.getLeft(registerMapping);
        Register right = node.getRight(registerMapping);

        if (right instanceof PhysicalRegister && right.equals(destination)) { // -> destination physical -> temp free
            if (commutative) {
                Register temp = left;
                left = right;
                right = temp;
            } else {
                move(right, PhysicalRegister.Temp);
                right = PhysicalRegister.Temp;
            }
        }
        assignTempIfVirtual(destination);
        move(left, physical(destination));
        builder.append(
            String.format(
                "%s %s, %s\n",
                assemblyInstructionName,
                right.registerName(),
                physical(destination).registerName()
            )
        );
        moveToStackIfVirtual(destination);
    }

    private void ctld() {
        builder.append("cltd\n");
    }

    private void divMod(DivModInstruction dm) {
        Register divisor = dm.getDivisor(registerMapping);
        moveToTempIfVirtual(divisor);
        builder.append(String.format("idivl %s\n", physical(divisor)));
        discardTemp();
    }

    private void returnInstruction(ReturnInstruction returnInstruction) {
        Register returnRegister = returnInstruction.getReturnRegister(registerMapping);
        if (returnRegister != PhysicalRegister.Return) move(returnRegister, PhysicalRegister.Return);
        if (maxStackUsage > 0)
            builder.append(String.format("addq $%d, %%rsp\n", maxStackUsage));
        builder.append("ret\n");
    }

    private void constInt(ConstIntInstruction constIntInstruction) {
        Register destination = constIntInstruction.getDestination(registerMapping);
        assignTempIfVirtual(destination);
        builder.append(String.format("movl $%d, %s\n", constIntInstruction.getValue(), physical(destination).registerName()));
        moveToStackIfVirtual(destination);
    }

    private PhysicalRegister physical(Register register) {
        if (register instanceof PhysicalRegister physicalRegister) return physicalRegister;
        if (storedInTemp == register) return PhysicalRegister.Temp;
        throw new IllegalStateException("Non physical register not in temp");
    }

    private boolean assignTempIfVirtual(Register register) {
        if (!(register instanceof VirtualRegister virtualRegister)) return false;
        if (storedInTemp != null) throw new IllegalStateException("temp register already occupied");
        storedInTemp = virtualRegister;
        return true;
    }

    private boolean moveToTempIfVirtual(Register register) {
        if (!assignTempIfVirtual(register)) return false;
        move(register, PhysicalRegister.Temp);
        return true;
    }

    private void discardTemp() {
        storedInTemp = null;
    }

    private boolean moveToStackIfVirtual(Register register) {
        if (!(register instanceof VirtualRegister virtualRegister)) return false;
        if (register != storedInTemp) throw new IllegalStateException("attempted to store temp in wrong register");
        move(PhysicalRegister.Temp, register);
        storedInTemp = null;
        return true;
    }

    private void move(Register from, Register to) {
        if (!(from instanceof PhysicalRegister) && !(to instanceof PhysicalRegister))
            throw new IllegalArgumentException("At least on register must be physical");
        if (from.equals(to)) return;
        builder.append(
            String.format(
                "movl %s, %s\n",
                from.registerName(),
                to.registerName()
            )
        );
    }
}
