// src/pages/MyPage/List.js
import React from "react";

const List = ({ items }) => {
    return (
        <div className="list">
            {items.length > 0 ? (
                items.map((item, index) => (
                    <div className="list-item" key={index}>
                        <img src={item.image || "/default-image.png"} alt={item.name} />
                        <div className="list-content">
                            <h3>{item.name}</h3>
                            <p>{item.description}</p>
                        </div>
                    </div>
                ))
            ) : (
                <p>데이터가 없습니다.</p>
            )}
        </div>
    );
};

export default List;
