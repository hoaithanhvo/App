package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nidecsnipeit.network.model.CategoryFieldModel;

import java.util.List;

public class CaterogyAdapter extends ArrayAdapter<CategoryFieldModel> {
    private Context context;
    private List<CategoryFieldModel> categories;

    public CaterogyAdapter(Context context, List<CategoryFieldModel> categories) {
        super(context, android.R.layout.simple_dropdown_item_1line, categories);
        this.context = context;
        this.categories = categories;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Kiểm tra nếu view chưa được tái sử dụng, tạo mới nếu cần
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        // Lấy đối tượng hiện tại
        CategoryFieldModel category = categories.get(position);

        // Lấy TextView từ layout và gán tên category
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(category.getName());  // Giả sử bạn có phương thức getName()

        return convertView;
    }
}
