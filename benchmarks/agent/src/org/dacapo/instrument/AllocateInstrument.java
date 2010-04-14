package org.dacapo.instrument;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class AllocateInstrument extends ClassAdapter {

	private static final String   LOG_INTERNAL_NAME         = "org/dacapo/instrument/Log";
	private static final String   LOG_REPORT_HEAP           = "reportHeap";
	private static final String   LOG_REPORT_HEAP_SIGNATURE = "()V"; 
	private static final String   LOG_INTERNAL_REPORT_HEAP  = "$$reportHeap";
	private static final String   LOG_METHOD_ALLOCATE       = "reportBlank";
	private static final String   LOG_METHOD_SIGNATURE      = "()V"; 

	private String  name;
	private int     access;
	private boolean done = false;
	private boolean containsAllocate = false;
	
	private static final String   INSTRUMENT_PACKAGE        = "org/dacapo/instrument/";

	public AllocateInstrument(ClassVisitor cv) {
		super(cv);
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.name   = name;
		this.access = access;
		super.visit(version, access, name, signature, superName, interfaces);
	}
		
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (!done && instrument() && instrument(access))
			return new AllocateInstrumentMethod(access, name, desc, signature, exceptions, super.visitMethod(access,name,desc,signature,exceptions));
		else
			return super.visitMethod(access,name,desc,signature,exceptions);
	}
	
	public void visitEnd() {
		if (!done) {
			done = true;
			if (containsAllocate) {
				try {
					Class k = Log.class;
					
					GeneratorAdapter mg;
					Label start;
					Label end;
	
					// generate Log monitorEnter function
					mg = new GeneratorAdapter(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, new Method(LOG_INTERNAL_REPORT_HEAP, LOG_REPORT_HEAP_SIGNATURE), LOG_REPORT_HEAP_SIGNATURE, new Type[] {}, this);
					
					start = mg.mark();
					mg.invokeStatic(Type.getType(k), Method.getMethod(k.getMethod(LOG_REPORT_HEAP)));
					end   = mg.mark();
					mg.returnValue();
					
					mg.catchException(start, end, Type.getType(Throwable.class));
					mg.returnValue();
					
					mg.endMethod();
				} catch (NoSuchMethodException nsme) {
					System.err.println("Unable to find Log.reportHeap method");
					System.err.println("M:"+nsme);
					nsme.printStackTrace();
				}
			}
		}
	}
	
	private boolean instrument() {
		return (access & Opcodes.ACC_INTERFACE) == 0 && !name.startsWith(INSTRUMENT_PACKAGE);
	}
	
	private boolean instrument(int access) {
		return (access & Opcodes.ACC_ABSTRACT) == 0 && (access & Opcodes.ACC_BRIDGE) == 0 && (access & Opcodes.ACC_NATIVE) == 0;
	}
	
	private class AllocateInstrumentMethod extends AdviceAdapter {
		AllocateInstrumentMethod(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
			super(mv, access, name, desc);
		}
		
		public void visitMultiANewArrayInsn(String desc, int dims) {
			super.visitMultiANewArrayInsn(desc, dims);
			addLog();
		}
		
		public void visitTypeInsn(int opcode, String type) {
			super.visitTypeInsn(opcode, type);
			if (opcode == Opcodes.NEW || opcode == Opcodes.ANEWARRAY) {
				containsAllocate = true;
				addLog();
			}
		}
		
		public void visitIntInsn(int opcode, int operand) {
			super.visitIntInsn(opcode, operand);
			if (opcode == Opcodes.NEWARRAY) {
				containsAllocate = true;
				addLog();
			}
		}
		
		private void addLog() {
			super.visitMethodInsn(Opcodes.INVOKESTATIC, name, LOG_INTERNAL_REPORT_HEAP, LOG_REPORT_HEAP_SIGNATURE);
			// invokestatic $$reportHeapGC
			// 
			/*
			Label target = super.newLabel();

			Label start = super.mark();
//			super.dup();
			super.visitMethodInsn(Opcodes.INVOKESTATIC, LOG_INTERNAL_NAME, LOG_METHOD_ALLOCATE, LOG_METHOD_SIGNATURE);
			Label end = super.mark();
//
			super.visitJumpInsn(Opcodes.GOTO,target);
			super.catchException(start,end,Type.getType(NoClassDefFoundError.class));
			super.visitInsn(Opcodes.NOP);
//			super.catchException(start,end,Type.getType(Exception.class));
//			super.pop(); // for some unknown reason using super.visitInsn(Opcodes.POP); here causes an indexing error in ASM
			super.visitJumpInsn(Opcodes.GOTO,target);
			super.mark(target);
			*/
		}
	}
}
