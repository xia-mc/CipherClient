package fr.crazycat256.cipherclient.transform.transformers;

import fr.crazycat256.cipherclient.events.EventManager;
import fr.crazycat256.cipherclient.events.custom.UpdateMoveStateEvent;
import fr.crazycat256.cipherclient.transform.Transform;
import fr.crazycat256.cipherclient.transform.Transformer;
import net.minecraft.util.MovementInputFromOptions;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MovementInputFromOptionsTransformer extends Transformer {

    public MovementInputFromOptionsTransformer() {
        super(MovementInputFromOptions.class);
    }

    /**
     * Calls {@link #postEvent()} before the method
     * @param mn MethodNode
     */
    @Transform(methodName = "updatePlayerMoveState")
    public void updatePlayerMoveState(MethodNode mn) {
        mn.instructions.insert(new MethodInsnNode(INVOKESTATIC, selfPath, "postEvent", "()V", false));
    }

    @SuppressWarnings("unused")
    public static void postEvent() {
        EventManager.postEvent(new UpdateMoveStateEvent());
    }
}
