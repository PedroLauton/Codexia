INSERT INTO categories (id, workspace_id, name, description, created_at, updated_at)
VALUES (
           'c0000000-0000-0000-0000-000000000001',
           'a0000000-0000-0000-0000-000000000001',
           'Algoritmos',
           'Categoria de algoritmos',
           NOW(),
           NOW()
       ) ON CONFLICT (id) DO NOTHING;