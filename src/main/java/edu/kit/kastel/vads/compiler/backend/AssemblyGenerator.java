package edu.kit.kastel.vads.compiler.backend;

import edu.kit.kastel.vads.compiler.backend.instruction.*;
import edu.kit.kastel.vads.compiler.backend.register.*;
import edu.kit.kastel.vads.compiler.ir.node.Block;
import org.jspecify.annotations.Nullable;

public class AssemblyGenerator {
    private final StringBuilder builder = new StringBuilder();
    private int comparisonLabelCounter = 0;

    // Temps for current function
    private FunctionInstructionSet instructionSet;
    private RegisterMapping registerMapping;
    private @Nullable VirtualRegister storedInTemp;
    private int maxStackUsage;

    public AssemblyGenerator() {
        addStarterCode();
    }

    public void addFunction(FunctionInstructionSet instructionSet, RegisterMapping registerMapping, int maxStackUsage) {
        this.instructionSet = instructionSet;
        this.registerMapping = registerMapping;
        this.storedInTemp = null;
        this.maxStackUsage = maxStackUsage;
        if (instructionSet.name() == "main") {
            builder.append(".main:\n");
        } else {
            builder.append(String.format("s:\n", instructionSet.name()));
        }
        if (maxStackUsage > 0) {
            builder.append(String.format("subq $%d, %%rsp\n", maxStackUsage));
        }
        for (Block block : instructionSet.getBlocks()) {
            addLabel(block);
            for (Instruction instruction : instructionSet.getInstructions(block)) {
                generateForInstruction(instruction);
            }
            builder.append('\n');
        }
    }

    public String getAssembly() {
        return builder.toString();
    }

    private void addStarterCode() {
        builder.append(".global main\n" +
                ".global .main\n" +
                ".text\n" +
                "main:\n" +
                "call .main\n" +
                "movq %rax, %rdi\n" +
                "movq $0x3C, %rax\n" +
                "syscall\n");
    }

    private void addLabel(Block block) {
        if (!(instructionSet.getInstruction(block, 0) instanceof LabelInstruction label)) {
            throw new RuntimeException("First instruction of block was not a label");
        }
        builder.append(String.format("%s:\n", label.label()));
    }

    private void generateForInstruction(Instruction instruction) {
        switch (instruction) {
            case MoveInstruction moveInstruction        -> addMove(moveInstruction);

            case AddInstruction add                     -> addBinary(add, "addl", true);
            case SubInstruction sub                     -> addBinary(sub, "subl", false);
            case MulInstruction mul                     -> addBinary(mul, "imull", true);
            case CtldInstruction _                      -> addCtld();
            case DivModInstruction dm                   -> addDivMod(dm);

            case BitAndInstruction bitAnd               -> addBinary(bitAnd,"and", true);
            case BitOrInstruction bitOr                 -> addBinary(bitOr,"or", true);
            case BitXorInstruction bitXor               -> addBinary(bitXor,"xor", true);
            case BitNegationInstruction bitNegation     -> addBitNegation(bitNegation);

            case ShiftLeftInstruction shiftLeft         -> addShift(shiftLeft, "sal");
            case ShiftRightInstruction shiftRight       -> addShift(shiftRight, "sar");

            case EqualsInstruction equals               -> addComparison(equals, "je");
            case UnequalsInstruction unequals           -> addComparison(unequals, "jne");
            case SmallerInstruction smaller             -> addComparison(smaller, "jl");
            case SmallerEqInstruction smallerEq         -> addComparison(smallerEq, "jle");
            case BiggerInstruction bigger               -> addComparison(bigger, "jg");
            case BiggerEqInstruction biggerEq           -> addComparison(biggerEq, "jge");
            case LogNegationInstruction logNegation     -> addLogNegation(logNegation);

            case ConstIntInstruction constInt           -> addConstInt(constInt);
            case ConstBoolInstruction constBool         -> addConstBool(constBool);

            case JumpInstruction jump                   -> addJump(jump);
            case JumpZeroInstruction jumpZero           -> addJumpZero(jumpZero);
            case JumpNonZeroInstruction jumpNonZero     -> addJumpNonZero(jumpNonZero);


            case ReturnInstruction ret                  -> addReturnInstruction(ret);
            case LabelInstruction _                     -> {}
        }
    }

    private void addMove(MoveInstruction moveInstruction) {
        Register destination = moveInstruction.getDestination(registerMapping);
        Register source = moveInstruction.getSource(registerMapping);
        if (source instanceof VirtualRegister && destination instanceof VirtualRegister) {
            move(source, PhysicalRegister.Temp);
            move(PhysicalRegister.Temp, destination);
        } else {
            move(source, destination);
        }
    }

    private void addBinary(BinaryOperationInstruction binOp, String assemblyInstructionName, boolean commutative) {
        Register destination = binOp.getDestination(registerMapping);
        Register left = binOp.getLeft(registerMapping);
        Register right = binOp.getRight(registerMapping);

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

    private void addComparison(BinaryOperationInstruction binOp, String jumpInstruction) {
        Register destination = binOp.getDestination(registerMapping);
        Register left = binOp.getLeft(registerMapping);
        Register right = binOp.getRight(registerMapping);
        if (left instanceof VirtualRegister && right instanceof VirtualRegister) {
            // cmp first second computes second - first => first = right, second = left
            move(right, PhysicalRegister.Temp);
            builder.append(String.format("cmp %s, %s\n", PhysicalRegister.Temp.registerName(), left.registerName()));
        }
        else {
            // cmp first second computes second - first => first = right, second = left
            builder.append(String.format("cmp %s, %s\n", right.registerName(), left.registerName()));
        }
        String labelTrue = String.format(".C%dT", comparisonLabelCounter);
        String labelEnd = String.format(".C%dE", comparisonLabelCounter);
        comparisonLabelCounter++;

        builder.append(String.format("%s %s\n", jumpInstruction, labelTrue));
        builder.append(String.format("movl $%d, %s\n", 0, destination.registerName()));
        builder.append(String.format("jmp %s\n", labelEnd));
        builder.append(String.format("%s:\n", labelTrue));
        builder.append(String.format("movl $%d, %s\n", 1, destination.registerName()));
        builder.append(String.format("%s:\n", labelEnd));
    }

    public void addShift(ShiftInstruction shift, String shiftAsmInstruction) {
        Register destination = shift.getDestination(registerMapping);
        builder.append(String.format(
                "%s %s, %s\n",
                shiftAsmInstruction,
                PhysicalRegister.ShiftRegister.registerName1Byte(),
                destination.registerName()
        ));
    }

    private void addBitNegation(BitNegationInstruction bitNegation) {
        Register destination = bitNegation.getDestination(registerMapping);
        Register source = bitNegation.getSource(registerMapping);

        if (destination == source) {
            builder.append(String.format("not %s\n", destination));
            return;
        }
        if (destination instanceof VirtualRegister) {
            assignTempIfVirtual(destination);
        }
        move(source, physical(destination));
        builder.append(String.format("not %s\n", physical(destination)));
        moveToStackIfVirtual(destination);
    }

    private void addLogNegation(LogNegationInstruction logNegation) {
        Register destination = logNegation.getDestination(registerMapping);
        builder.append(String.format("subl $%d, %s\n", 1, destination));
    }

    private void addJump(JumpInstruction jump) {
        builder.append(String.format("jmp %s\n", jump.target().label()));
    }

    private void addJumpZero(JumpZeroInstruction jump) {
        Register destination = jump.register(registerMapping);
        builder.append(String.format("cmp $%d, %s\n", 0, destination.registerName()));
        builder.append(String.format("jz %s\n", jump.target().label()));
    }

    private void addJumpNonZero(JumpNonZeroInstruction jump) {
        Register destination = jump.register(registerMapping);
        builder.append(String.format("cmp $%d, %s\n", 0, destination.registerName()));
        builder.append(String.format("jnz %s\n", jump.target().label()));
    }

    private void addCtld() {
        builder.append("cltd\n");
    }

    private void addDivMod(DivModInstruction dm) {
        Register divisor = dm.getDivisor(registerMapping);
        moveToTempIfVirtual(divisor);
        builder.append(String.format("idivl %s\n", physical(divisor)));
        discardTemp();
    }





    private void addConstInt(ConstIntInstruction constIntInstruction) {
        Register destination = constIntInstruction.getDestination(registerMapping);
        assignTempIfVirtual(destination);
        builder.append(String.format("movl $%d, %s\n", constIntInstruction.getValue(), physical(destination).registerName()));
        moveToStackIfVirtual(destination);
    }

    private void addConstBool(ConstBoolInstruction constBool) {
        Register destination = constBool.getDestination(registerMapping);
        assignTempIfVirtual(destination);
        int num = constBool.getValue() ? 1 : 0;
        builder.append(String.format("movl $%d, %s\n", num, physical(destination).registerName()));
        moveToStackIfVirtual(destination);
    }




    private void addReturnInstruction(ReturnInstruction returnInstruction) {
        Register returnRegister = returnInstruction.getReturnRegister(registerMapping);
        if (returnRegister != PhysicalRegister.Return) move(returnRegister, PhysicalRegister.Return);
        if (maxStackUsage > 0)
            builder.append(String.format("addq $%d, %%rsp\n", maxStackUsage));
        builder.append("ret\n");
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
        if (!(register instanceof VirtualRegister)) return false;
        if (register != storedInTemp) throw new IllegalStateException("attempted to store temp in wrong register");
        move(PhysicalRegister.Temp, register);
        storedInTemp = null;
        return true;
    }

    private void move(Register from, Register to) {
        if (!(from instanceof PhysicalRegister) && !(to instanceof PhysicalRegister)) {
            throw new IllegalArgumentException("At least on register must be physical");
        }
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
