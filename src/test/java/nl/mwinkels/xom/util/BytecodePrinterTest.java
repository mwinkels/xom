package nl.mwinkels.xom.util;

import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BytecodePrinterTest {

    private Bytecode bytecode;
    private BytecodePrinter bytecodePrinter;

    @Before
    public void init() {
        bytecode = new Bytecode(new ConstPool("not-used"));
    }

    @Test
    public void shouldPrintMnemonic() throws Throwable {
        bytecode.addIconst(0);
        bytecodePrinter = new BytecodePrinter(bytecode.toCodeAttribute());
        assertThat(bytecodePrinter.makeString(), is("0 iconst_0"));
    }

    @Test
    public void shouldPrintMnemonics() throws Throwable {
        bytecode.addIconst(0);
        bytecode.addAstore(2);
        bytecodePrinter = new BytecodePrinter(bytecode.toCodeAttribute());
        assertThat(bytecodePrinter.makeString(), is("0 iconst_0\n1 astore_2"));
    }

    @Test
    public void shouldPrintIndexOnLine() throws Throwable {
        bytecode.addOpcode(Opcode.GOTO);
        bytecode.addIndex(15);
        bytecode.addIconst(0);
        bytecodePrinter = new BytecodePrinter(bytecode.toCodeAttribute());
        assertThat(bytecodePrinter.makeString(), is("0 goto 15{2}\n3 iconst_0"));
    }

    @Test
    public void shouldPrintNegativeIndexOnLine() throws Throwable {
        bytecode.addOpcode(Opcode.GOTO);
        bytecode.addIndex(-15);
        bytecode.addIconst(0);
        bytecodePrinter = new BytecodePrinter(bytecode.toCodeAttribute());
        assertThat(bytecodePrinter.makeString(), is("0 goto -15{2}\n3 iconst_0"));
    }

}
