package inventory_management.data;

/**
 * Created by Kh3m on 7/4/2018.
 */
public class InventoryContract {

    public static final String COLUMN_ALIAS_COUNT = "count";
    public static final String COLUMN_ALIAS_SUM = "sum";
    public static final String COLUMN_ALIAS_YEAR = "year";
    public static final String COLUMN_ALIAS_MONTH = "month";
    public static final String COLUMN_ALIAS_TOTAL = "total";

    public class CategoryEntry {

        public static final String TABLE_NAME = "category";

        public static final String _ID = "cat_id";
        public static final String COLUMN_CATEGORY_NAME = "cat_name";
        public static final String COLUMN_CATEGORY_DESCRIPTION = "cat_description";
    }

    public class ItemEntry {

        public static final String TABLE_NAME = "stock";

        public static final String _ID = "stock_id";
        public static final String COLUMN_CAT_ID = "cat_id";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_TOTAL_QUANTITY = "quantity";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_TCU = "tcu";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CREATED_ON = "created_on";
    }

    public class SalesEntry {

        public static final String TABLE_NAME = "sales";

        public static final String _ID = "sales_id";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SALE_PRICE = "sale_price";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CREATED_ON = "created_on";

    }

    public class StockHisEntry {

        public static final String TABLE_NAME = "stock_his";

        public static final String _ID = "stock_his_id";
        public static final String COLUMN_STOCK_ID = ItemEntry._ID;
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT_COST = "unit_cost";
        public static final String COLUMN_TCU = "tcu";
        public static final String COLUMN_LAST_UPDATED = "last_updated";

    }

    public class RoleEntry {
        public static final String TABLE_NAME = "role";

        public static final String _ID = "role_id";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_ROLE_DESC = "role_desc";
    }

    public class UserEntry {
        public static final String TABLE_NAME = "users";

        public static final String _ID = "user_id";
        public static final String COLUMN_ROLE_ID = RoleEntry._ID;
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_IMAGE = "image";
    }

    public class EventEntry {
        public static final String TABLE_NAME = "events";

        public static final String _ID = "event_id";
        public static final String COLUMN_USER_ID = UserEntry._ID;
        public static final String COLUMN_ACTION = "action";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_DATE = "timestamp";
    }
}
