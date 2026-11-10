import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;
import java.util.Optional;

/**
 * A backpack that holds up to {@value #MAX_SLOTS} items.
 *
 * The backpack is a {@link GameObject}: its own rectangle and texture draw the bag icon,
 * and a row of numbered slots is drawn beside it. Keys 1-5 select the slot with that number.
 *
 * Textures and the font are owned by the screen that created them, not by this class.
 */
public class Backpack extends GameObject {

    /** Item that can be stored in the backpack. */
    public record Item(String name, Texture icon) {
        public Item {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(icon, "icon");
        }
    }

    public static final int MAX_SLOTS = 5;

    // World units, matching the viewport used by GameScreen
    private static final float SLOT_SIZE = 1f;
    private static final float SLOT_PADDING = 0.2f;
    private static final float ICON_INSET = 0.1f;
    private static final Color SELECTED_TINT = Color.YELLOW;

    // Slot number labels, in world units (cap height of the digit, and gap between slot top and digit)
    private static final float LABEL_CAP_HEIGHT = 0.25f;
    private static final float LABEL_GAP = 0.08f;

    private final Array<Item> items = new Array<>(MAX_SLOTS);
    private final Texture slotTexture;
    private final BitmapFont font;
    private final String[] slotLabels = new String[MAX_SLOTS];
    private final GlyphLayout labelLayout = new GlyphLayout();
    private final Color previousFontColor = new Color();
    private final Vector2 slotPosition = new Vector2();

    private int selectedSlot = 0;

    /**
     * Creates a backpack.
     *
     * @param x           x position of the bag icon
     * @param y           y position of the bag icon
     * @param w           width of the bag icon
     * @param h           height of the bag icon
     * @param texture     texture of the bag icon
     * @param slotTexture texture drawn behind each slot
     * @param font        font used to draw the slot numbers (its size and colour are restored after drawing)
     */
    public Backpack(float x, float y, float w, float h, Texture texture, Texture slotTexture, BitmapFont font) {
        super(x, y, w, h, texture);
        this.slotTexture = Objects.requireNonNull(slotTexture, "slotTexture");
        this.font = Objects.requireNonNull(font, "font");

        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            slotLabels[slot] = Integer.toString(slot + 1);
        }
    }

    /**
     * Adds an item to the first free slot.
     *
     * @return {@code true} if the item was stored, {@code false} if the backpack is full
     */
    public boolean addItem(Item item) {
        Objects.requireNonNull(item, "item");
        if (isFull()) {
            return false;
        }
        items.add(item);
        return true;
    }

    /**
     * Removes and returns the item in the given slot. Items after it shift down one slot.
     *
     * @return the removed item, or empty if the slot is out of range or unoccupied
     */
    public Optional<Item> removeItem(int slot) {
        if (slot < 0 || slot >= items.size) {
            return Optional.empty();
        }
        return Optional.of(items.removeIndex(slot));
    }

    // Removes and returns the item in the currently selected slot, if any.
    public Optional<Item> removeSelectedItem() {
        return removeItem(selectedSlot);
    }

    // Returns the item in the given slot, if any.
    public Optional<Item> getItem(int slot) {
        if (slot < 0 || slot >= items.size) {
            return Optional.empty();
        }
        return Optional.of(items.get(slot));
    }

    // Returns the item in the currently selected slot.
    public Optional<Item> getSelectedItem() {
        return getItem(selectedSlot);
    }

    public boolean contains(String itemName) {
        for (var item : items) {
            if (item.name().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return items.size >= MAX_SLOTS;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.size;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < MAX_SLOTS) {
            selectedSlot = slot;
        }
    }

    public void clear() {
        items.clear();
        selectedSlot = 0;
    }

    // Polls the number keys 1-5 to change the selected slot.
    @Override
    public void update(float deltaTime) {
        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + slot)) {
                selectSlot(slot);
            }
        }
    }

    // Draws the bag icon, then every slot with its item, then the slot numbers above the slots.
    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            computeSlotPosition(slot, slotPosition);

            batch.setColor(slot == selectedSlot ? SELECTED_TINT : Color.WHITE);
            batch.draw(slotTexture, slotPosition.x, slotPosition.y, SLOT_SIZE, SLOT_SIZE);
            batch.setColor(Color.WHITE);

            if (slot < items.size) {
                float iconSize = SLOT_SIZE - 2 * ICON_INSET;
                batch.draw(items.get(slot).icon(),
                    slotPosition.x + ICON_INSET,
                    slotPosition.y + ICON_INSET,
                    iconSize,
                    iconSize);
            }
        }

        drawSlotNumbers(batch);
    }

    /**
     * Draws "1" to "5" centred just above each slot, matching the number keys. Sitting above
     * the slot keeps the digits clear of the item icons.
     *
     * The font is rescaled so the digits are LABEL_CAP_HEIGHT tall in world units.
     * Its scale and colour are put back afterwards because the same font is shared with the rest of the screen.
     */
    private void drawSlotNumbers(Batch batch) {
        float previousScaleX = font.getScaleX();
        float previousScaleY = font.getScaleY();
        previousFontColor.set(font.getColor());

        float unscaledCapHeight = font.getCapHeight() / previousScaleY;
        font.getData().setScale(LABEL_CAP_HEIGHT / unscaledCapHeight);

        for (int slot = 0; slot < MAX_SLOTS; slot++) {
            computeSlotPosition(slot, slotPosition);

            font.setColor(slot == selectedSlot ? SELECTED_TINT : Color.WHITE);
            labelLayout.setText(font, slotLabels[slot]);

            // BitmapFont draws downwards from y, so add the cap height to rest the digit on the gap
            float labelX = slotPosition.x + (SLOT_SIZE - labelLayout.width) / 2f;
            float labelY = slotPosition.y + SLOT_SIZE + LABEL_GAP + LABEL_CAP_HEIGHT;
            font.draw(batch, labelLayout, labelX, labelY);
        }

        font.getData().setScale(previousScaleX, previousScaleY);
        font.setColor(previousFontColor);
    }

    /** Slots run left to right, starting just right of the bag icon. */
    private void computeSlotPosition(int slot, Vector2 target) {
        float startX = rect.x + rect.width + SLOT_PADDING;
        target.set(startX + slot * (SLOT_SIZE + SLOT_PADDING), rect.y);
    }
}
