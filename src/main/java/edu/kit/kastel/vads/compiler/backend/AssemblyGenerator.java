package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.register.PhysicalRegister;
import edu.kit.kastel.vads.compiler.backend.register.Register;
import edu.kit.kastel.vads.compiler.backend.register.RegisterAllocator;
import edu.kit.kastel.vads.compiler.backend.register.VirtualRegister;
import edu.kit.kastel.vads.compiler.ir.node.*;
import org.jspecify.annotations.Nullable;

import static edu.kit.kastel.vads.compiler.ir.util.NodeSupport.predecessorSkipProj;

public class AssemblyGenerator {
    private final StringBuilder builder = new StringBuilder();
    private final RegisterAllocator registerAllocator;
    private @Nullable VirtualRegister storedInTemp;

    public AssemblyGenerator(InstructionBlock block, RegisterAllocator registerAllocator) {
        this.registerAllocator = registerAllocator;
        storedInTemp = null;
        for (Instruction instruction : block.getInstructions()) {
            generateForInstruction(instruction);
        }
    }

    public String getAssembly() {
        return builder.toString();
    }

    private void generateForInstruction(Instruction instruction) {
        switch (instruction.getNode()) {
            case AddNode add -> binary(add, "addl");
            case SubNode sub -> binary(sub, "subl");
            case MulNode mul -> binary(mul, "subl");
            case DivNode div -> divMod(div);
            case ModNode mod -> divMod(mod);
            case ReturnNode r -> returnInstruction(r);
            case ConstIntNode c -> constInt(c);
            case Phi _ -> throw new UnsupportedOperationException("phi");
            case Block _, ProjNode _, StartNode _ -> {}
        }
    }

    private void binary(BinaryOperationNode node, String assemblyInstructionName) {
        Register destination = registerAllocator.get(node);
        Register left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        Register right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));
        if (destination == null || left == null || right == null) throw new IllegalStateException("Unallocated registers");

        assignTempIfVirtual(destination);
        move(physical(destination), left);
        builder.append(
                String.format(
                        "%s %s, %s\n",
                        assemblyInstructionName,
                        physical(destination).registerName(),
                        right.registerName()
                )
        );
        moveToStackIfVirtual(destination);
    }

    private void divMod(BinaryOperationNode node) {
        Register destination = registerAllocator.get(node);
        Register left = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.LEFT));
        Register right = registerAllocator.get(predecessorSkipProj(node, BinaryOperationNode.RIGHT));
        if (destination == null || left == null || right == null) throw new IllegalStateException("Unallocated registers");

        if (left != PhysicalRegister.DividendLS) move(left, PhysicalRegister.DividendLS);
        builder.append("cltd\n");
        moveToTempIfVirtual(right);
        builder.append(String.format("idivl %s\n", physical(right).registerName()));
        discardTemp();
        if (node instanceof DivNode && destination != PhysicalRegister.Quotient)
            move(PhysicalRegister.Quotient, destination);
        if (node instanceof ModNode && destination != PhysicalRegister.Remainder)
            move(PhysicalRegister.Remainder, destination);
    }

    private void returnInstruction(ReturnNode returnNode) {
        Register returnRegister = registerAllocator.get(returnNode);
        if (returnRegister == null) throw new IllegalStateException("Unallocated register");
        if (returnRegister != PhysicalRegister.Return) move(returnRegister, PhysicalRegister.Return);
        builder.append("ret\n");
    }

    private void constInt(ConstIntNode constIntNode) {
        Register destination = registerAllocator.get(constIntNode);
        if (destination == null) throw new IllegalStateException("Unallocated register");
        assignTempIfVirtual(destination);
        builder.append(String.format("movl $%d, %s", constIntNode.value(), physical(destination).registerName()));
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
        builder.append(
            String.format(
                "movl %s, %s\n",
                from.registerName(),
                to.registerName()
            )
        );
    }
}
