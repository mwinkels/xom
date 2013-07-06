package nl.mwinkels.xom.util;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Mnemonic;


public class BytecodePrinter {

    private static final char SPACE = ' ';
    private static final char NEW_LINE = '\n';
    private CodeAttribute codeAttribute;

    public BytecodePrinter(CodeAttribute codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    public String makeString() throws BadBytecode {
        StringBuilder stringBuilder = new StringBuilder();
        CodeIterator iterator = codeAttribute.iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            stringBuilder.append(index).append(SPACE);
            stringBuilder.append(Mnemonic.OPCODE[iterator.byteAt(index)]);
            if (iterator.hasNext()) {
                int offset = iterator.lookAhead() - index - 1;
                switch (offset) {
                    case 2:
                        stringBuilder.append(SPACE);
                        stringBuilder.append(iterator.s16bitAt(index + 1));
                        stringBuilder.append("{2}");
                        break;
                    case 1:
                        stringBuilder.append(SPACE);
                        stringBuilder.append(iterator.byteAt(index + 1));
                        stringBuilder.append("{1}");
                        break;
                    case 0:
                        break;
                    default:
                        stringBuilder.append("{").append(offset).append("}");

                }
                stringBuilder.append(NEW_LINE);
            }
        }
        return stringBuilder.toString();
    }
}
