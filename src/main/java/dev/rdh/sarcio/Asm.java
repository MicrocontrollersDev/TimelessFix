package dev.rdh.sarcio;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

public final class Asm {
	private Asm() {}

	public static void asmLazyLoadBase(ClassNode cn) {
		MappingResolver mr = FabricLoader.getInstance().getMappingResolver();
		String iClassName = "net.minecraft.unmapped.C_85792774";
		String className = mr.mapClassName("intermediary", iClassName);
		String valueName = mr.mapFieldName("intermediary", iClassName, "f_47053246", "Ljava/lang/Object;");
		String loadedName = mr.mapFieldName("intermediary", iClassName, "f_76771970", "Z");
		String loadName = mr.mapMethodName("intermediary", iClassName, "m_39421333", "()Ljava/lang/Object;");
		String getValueName = mr.mapMethodName("intermediary", iClassName, "m_33878528", "()Ljava/lang/Object;");

		FieldNode loaded = cn.fields.stream()
			.filter(f -> f.name.equals(loadedName) && f.desc.equals("Z"))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("no " + loadedName + " in " + className));
		loaded.access |= Opcodes.ACC_VOLATILE;

		MethodNode getValue = cn.methods.stream()
			.filter(m -> m.name.equals(getValueName) && m.desc.equals("()Ljava/lang/Object;"))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("no " + getValueName + " in " + className));

		String owner = cn.name;
		Type self = Type.getObjectType(owner);
		Label syncStart = new Label();
		Label syncEnd = new Label();
		Label handler = new Label();
		Label handlerEnd = new Label();
		Label unlock = new Label();
		Label ret = new Label();

		MethodNode built = make(i -> {
			i.visitTryCatchBlock(syncStart, syncEnd, handler, null);
			i.visitTryCatchBlock(handler, handlerEnd, handler, null);

			i.load(0, self);
			i.getfield(owner, loadedName, "Z");
			i.ifne(ret);
			i.load(0, self);
			i.monitorenter();

			i.visitLabel(syncStart);
			i.load(0, self);
			i.getfield(owner, loadedName, "Z");
			i.ifne(unlock);
			i.load(0, self);
			i.load(0, self);
			i.invokevirtual(owner, loadName, "()Ljava/lang/Object;", false);
			i.putfield(owner, valueName, "Ljava/lang/Object;");
			i.load(0, self);
			i.iconst(1);
			i.putfield(owner, loadedName, "Z");

			i.visitLabel(unlock);
			i.load(0, self);
			i.monitorexit();

			i.visitLabel(syncEnd);
			i.goTo(ret);

			i.visitLabel(handler);
			i.store(1, OBJECT);
			i.load(0, self);
			i.monitorexit();

			i.visitLabel(handlerEnd);
			i.load(1, OBJECT);
			i.athrow();

			i.visitLabel(ret);
			i.load(0, self);
			i.getfield(owner, valueName, "Ljava/lang/Object;");
			i.areturn(OBJECT);
		});

		getValue.instructions = built.instructions;
		getValue.tryCatchBlocks = built.tryCatchBlocks;
		getValue.localVariables = null;
		getValue.visibleLocalVariableAnnotations = null;
		getValue.invisibleLocalVariableAnnotations = null;
		getValue.maxStack = 2;
		getValue.maxLocals = 2;
	}

	private static final Type OBJECT = Type.getObjectType("java/lang/Object");

	private static MethodNode make(Consumer<InstructionAdapter> consumer) {
		MethodNode mn = new MethodNode();
		consumer.accept(new InstructionAdapter(mn));
		return mn;
	}
}
